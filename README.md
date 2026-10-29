All commits and the development history are available on [GitHub](https://github.com/introvert0v/tracefinder/tree/main). See the [full commit history](https://github.com/introvert0v/tracefinder/commits/main/).

# TraceFinder

A Java command-line tool that reads an access log, checks event levels against a CSV rulebook and writes a text report for review.

## Requirements

- JDK 11 or newer.
- Maven 3.6.3 or newer.
- Internet access for Maven to download dependencies on the first build.

Check your environment:

```bash
java -version
mvn -v
```

## Build and run

```bash
git clone https://github.com/introvert0v/tracefinder.git
cd tracefinder
```

Run the following commands from the directory containing `pom.xml`:

```bash
mvn clean package
java -jar target/tracefinder.jar logs.txt rules.csv report.txt
```

The build runs the tests and creates `target/tracefinder.jar`. Supply your own log file, or save the example below as `logs.txt`. The repository includes `rules.csv`.

Exactly three arguments are required:

| Position | Argument | Purpose |
| --- | --- | --- |
| 1 | `logs.txt` | Input access log |
| 2 | `rules.csv` | Levels and severity scores |
| 3 | `report.txt` | Output report |

Names are not fixed; relative and absolute paths are accepted. Quote paths containing spaces. The report's parent directory must already exist. An existing output file is overwritten, so use a different path from either input file.

## Log format

Each line contains five fields separated by `|`:

```text
timestamp | level | source IP | target | action
```

Example `logs.txt`:

```text
2024-03-15 02:14:08 | INFO | 192.168.1.45 | /login | success
2024-03-15 02:14:33 | WARN | 203.0.113.42 | /login | failed
2024-03-15 02:15:02 | INFO | 203.0.113.42 | /login | success
2024-03-15 02:16:02 | ALERT | 203.0.113.42 | /etc/passwd | read
2024-03-15 02:17:00 | DEBUG | 203.0.113.42 | /test | accessed
broken line
```

Timestamps use `yyyy-MM-dd HH:mm:ss` and are checked as actual dates and times. Surrounding field whitespace is trimmed for analysis; the original line is kept for the report.

Blank lines, invalid timestamps and lines with other than five pipe-separated fields are recorded as malformed. They do not stop processing.

## Rulebook

The header must be exactly `level,severity_score`. Each following line contains a non-empty level and an integer score:

```csv
level,severity_score
INFO,1
WARN,3
ERROR,5
ALERT,9
```

The reader supports this simple, unquoted two-column CSV format. Duplicate levels, missing or extra columns, empty levels and non-integer scores are rejected. Quoted CSV fields are not supported.

Matching is case-sensitive: `info` does not match `INFO`. A correctly parsed entry whose level is absent from the rulebook is an unknown pattern, not a malformed line.

## Report

Every report starts with a title and generation time, followed by these sections:

| Section | Contents |
| --- | --- |
| Activity Summary | Counts for the levels defined in the rulebook; unknown levels are excluded |
| Flagged Entries | Original lines with scores of **3 or higher**, sorted by score descending |
| Suspicious Activity by IP | IPs with at least one flagged entry, sorted by total valid entry count descending |
| Unknown Patterns | Original lines with unknown levels and their source line numbers |
| Malformed Lines | Invalid original lines and their source line numbers |

A suspicious IP's count includes **all valid entries** for that IP, including low-scoring and unknown levels. Malformed lines are excluded from analysis. Source line numbers start at 1 and include blank and malformed lines.

All five section headers are always present. Empty sections contain `None found`. Activity Summary lists levels alphabetically; if no known entries exist, it also displays `None found`.

For the example above, the summary counts are `ALERT: 1`, `ERROR: 0`, `INFO: 2` and `WARN: 1`. The ALERT line appears before the WARN line, `203.0.113.42` has **4 entries**, DEBUG is unknown on line 5, and line 6 is malformed.

## Error handling

The application prints an error to standard error and exits with code `1` for:

- An argument count other than three or an invalid path.
- A missing, unreadable or non-file input path.
- An invalid rulebook.
- A failure while reading input or writing the report.

Successful execution prints the report path and exits with code `0`. Malformed log lines and unknown levels are included in the report instead of causing failure.

## Tests

```bash
mvn test
```

The project uses JUnit 5. The current suite contains 12 test methods across five classes:

| Test class | Checks |
| --- | --- |
| `LogReaderTest` | Field parsing, original text, whitespace, invalid dates, field counts, blank lines, line numbering, empty input and rejection of semicolon separators |
| `RulebookReaderTest` | Rule parsing, whitespace and invalid rulebook contents |
| `LogProcessorTest` | Scores below and at the threshold, known-level counts, unknown entries, IP totals, severity ordering and case-sensitive matching (`info` is unknown when only `INFO` is defined) |
| `ReportWriterTest` | Empty sections, section order, original flagged lines, IP ordering, source line numbers and a log containing only malformed lines (four `None found` sections followed by numbered original lines) |
| `ReaderFailureTest` | Propagation of simulated read errors |

Tests create their data in memory using strings and `Reader` objects. They do not open, read or write data files. Maven stores test results in `target/surefire-reports/`.

## Project structure

Production Java files remain in the repository root. `pom.xml` explicitly configures that source directory and selects root-level `*.java` files. Tests are kept separately in `src/test/java`.

| File | Responsibility |
| --- | --- |
| `Main.java` | Validates arguments and paths, calls each stage and handles failures |
| `RulebookReader.java` | Reads and validates the rulebook |
| `LogReader.java` | Parses log lines and records malformed lines |
| `LogProcessor.java` | Matches levels, counts activity, identifies suspicious IPs and sorts flagged entries |
| `ReportWriter.java` | Formats the five report sections, orders IPs for display and writes the report |
| `LogEntry.java` | Stores parsed fields and the original line |
| `MalformedLine.java` | Stores an invalid line with its source line number |
| `LogReadResult.java` | Holds valid entries and malformed lines |
| `AnalysisResult.java` | Holds the analysis findings |

`Main` passes results between the readers, processor and writer. These stages do not call one another. Reader overloads allow tests to supply in-memory input; `formatReport()` returns report text without writing a file.
