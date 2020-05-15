#!/bin/bash

# ./csv-file-generator.sh -l 10 -n abc.csv -c 6 -o /opt/test
# -l lines
# -n file name
# -c columns for csv
# -o output path [optional]

WORK_HOME=$(cd "$(dirname "$0")/.."; pwd)
LINES=100
OUTPUT_DIR=${WORK_HOME}/out
NAME="test.csv"
COLUMNS=4

while getopts ':l:o:n:c:' OPTION;do
    case ${OPTION} in
        l)
            LINES=${OPTARG}
            ;;
        o)
            OUTPUT_DIR=${OPTARG}
            ;;
        n)
            NAME=${OPTARG}
            ;;
        c)
            COLUMNS=${OPTARG}
            ;;
        ?)
            echo -n ""
            ;;
    esac
done

FILE_NAME=${NAME}
FILE_PATH_NAME=${OUTPUT_DIR}/${FILE_NAME}

if [[ -e ${OUTPUT_DIR} ]]; then
    echo -n ""
else
    mkdir -p ${OUTPUT_DIR}
fi
if [[ -e ${FILE_PATH_NAME} ]]; then
    echo -n ""
else
    touch ${FILE_PATH_NAME}
fi

JAVA_OPTS="-Dcsv.lines=${LINES}"
JAVA_OPTS="${JAVA_OPTS} -Dcsv.columns=${COLUMNS}"
JAVA_OPTS="${JAVA_OPTS} -Dcsv.file.path.name=${FILE_PATH_NAME}"

CLASSPATH="${CLASSPATH}:${WORK_HOME}/lib/*"

java ${JAVA_OPTS} -classpath ${CLASSPATH} com.github.johnsonmoon.commons.CsvGenerator