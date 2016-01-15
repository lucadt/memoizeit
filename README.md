# MemoizeIt
MemoizeIt is the name of the approach to find memoization opportunities presented 
in our OOPSLA [article][paper].

This a very short tutorial that shows how to build MemoizeIt and that briefly explains 
how to get started with the MemoizeIt toolchain.

## Documentation
The steps and the input to fully reproduce the results of our work can 
be found in the archive under 'Source materials' [here][paper].
The archive contains the virtual-machine image submitted for the OOPSLA artifact evaluation.
Inside the archive there is a document containing the steps necessary to reproduce the experiments presented in the paper.

### Folders
This table provides a short description for each source folder in the repository:


Folder name | Notes
----------- | -----
memoizeit/jars | Jar dependencies for compilation and execution.
memoizeit/python | The directory contains the main execution runner for MemoizeIt.
memoizeit/src-analysis | The source-code of the analyses for the Field access profiling and Input-output profiling.
memoizeit/src-profiler-fields | Source-code of Field access profiling.
memoizeit/src-profiler-tuples | Source-code of Input-output profiling.
memoizeit/black\_list.txt | Contains a list of not supported methods that cause MemoizeIt to crash.
memoizeit/options.json | Legacy file kept for compatibility, used to setup various profiler output file names.

In case of additional questions or help with the tool please
please contact @lucadt [email](mailto:luca.dellatoffola@inf.ethz.ch) directly.

## Using MemoizeIt

### Dependencies 
The instrumentation framework and the offline data analysis of MemoizeIt are written in Java.
The implementation of the approach main algorithm and the scripts tailored for each analyzed program
are written in Python.  
The minimum software requirements to run MemoizeIt are:
- Java Virtual Machine (Java version 6 or greater)
- Python (version 2.7 or greater)
- Apache Ant (version 1.9 or greater)

### Building
Build MemoizeIt with the command:
```bash
ant clean && ant
```
The command will create two directories `memoizeit/jars` and `memoizeit_libs/jars` containing the compiled code.

### Executing
The distributed code is self-contained with the dependencies placed in the repository inside the directories
`memoizeit/libs` and `memoizeit_libs/libs`.
MemoizeIt runs via the command-line Python script `memoizeit/python/all.py`. 
To display all the command-line options use the command:
```bash
python memoizeit/python/all.py --help
```
The output of the command is supposed to be:
```
usage: all.py [-h] [--path PATH] [--folder FOLDER] [--time] [--fields]
              [--memo] [--program PROGRAM] [--ranking] [--descriptions]
              [--limit LIMIT]

MemoizeIt - Finding memoization opportunities.

optional arguments:
  -h, --help         show this help message and exit
  --path PATH        specifies the working directory
  --folder FOLDER    specify where to save the profiled data
  --time             Run the initial time profiling phase (Use pre-loaded
                     JVisual VM profiles)
  --fields           Run the initial field profiling phase
  --memo             Run the tuples profiling phase
  --program PROGRAM  Run the profiling for provided program
  --ranking          Print the ranking of the candidate methods
  --descriptions     Print the list of programs that can be analyzed and a
                     short description
  --limit LIMIT      Select iterative mode. Use MemoizeIt algorithm to
                     traverse all the depths argument is depth incremental
                     function [exhaustive, inc1, pow2 (default)]
```
The example command below executes a MemoizeIt complete analysis run for one of the programs that can be obtained with the option `--descriptions`.
```
python memoizeit/python/all.py --path `pwd` --time --fields --memo --folder [output-folder] --limit [depth-function] --program [program-name]
```
The parameter `[program-name]` is one of the identifiers returned by `--descriptions`. `[depth-function]` indicates the strategy to increment the depth of traversal, 
we used `pow2` for the iterative approach. To tag or name an experiment use the parameter `[output-folder]`, if no value is set MemoizeIt uses the current time and date
to name the output directory.

[paper]: http://dl.acm.org/citation.cfm?id=2814270.2814290 
