#!/bin/bash

# Directory containing the files
test_dir="test_input"

# Script to be called with each file name
test_script="./runTests.sh"

for i in {1..3}; do
  # Iterate over each file in the directory
  for file in "$test_dir"/*; do
      if [ -f "$file" ]; then
          # Call the second script with the file name as an argument
          "$test_script" "$file"
      fi
  done
done
