#!/bin/bash

test_input_file=$1

echo "Running ./runTests $1"
# Bash expects the last line of the file to be empty
echo "" >> "$test_input_file"

#file in format "test-input_{testCaseId}-{loadBalancing}-{matrixDivision}-{coordinateId}-{resolution}.csv"
test_case="$(cut -d'/' -f1 <<<$(echo "$test_input_file" | rev) | rev)"
test_case="$(cut -d'.' -f1 <<<"$test_case")"
test_case="$(cut -d'_' -f2 <<<"$test_case")"

test_output_file=$(date +"./test_output/test-output_${test_case}_%Y-%m-%d_%H-%M.csv");
> $test_output_file;

# first line format = "size,rect,output,iterations,loadBalancing,matrixDivision,flags"
# second line format = "threads, granularity"

# Outputs a CSV file
# each line is in format "t,g,programOutput"
# With $test_input_file's length - 1

first_line=$(head -n 1 "$test_input_file")
size="$(cut -d',' -f1 <<<"$first_line")"
rect="$(cut -d',' -f2 <<<"$first_line")"
output="$(cut -d',' -f3 <<<"$first_line")"
iterations="$(cut -d',' -f4 <<<"$first_line")"
loadBalancing="$(cut -d',' -f5 <<<"$first_line")"
matrixDivision="$(cut -d',' -f6 <<<"$first_line")"
flags="$(cut -d',' -f7 <<<"$first_line" | tr -d '\r')"

fixed_args="-s \"$size\" -r \"$rect\" -o \"$output\" -i $iterations -b $loadBalancing -m $matrixDivision ${flags:+-$flags}"
echo $fixed_args

first_line=true
#java_run_cmd_base="java -classpath \"out/production/ralitsa-mandelbrot;lib/commons-math3-3.6.1.jar;lib/commons-cli-1.8.0.jar\" Main"
java_run_cmd_base="java -classpath \"out/production/ralitsa-mandelbrot:lib/commons-math3-3.6.1.jar:lib/commons-cli-1.8.0.jar\" Main"

while read -r line
do
  thread_count="$(cut -d',' -f1 <<<"$line")"
  granularity="$(cut -d',' -f2 <<<"$line")"
  args="$fixed_args -t $thread_count -g $granularity"
  java_run_cmd="$java_run_cmd_base $args"
  echo $java_run_cmd
  res=$(eval "$java_run_cmd")
  echo "$thread_count,$granularity,$res" >> "$test_output_file"
  #Skip first line
done < <(tail -n +2 "$test_input_file")