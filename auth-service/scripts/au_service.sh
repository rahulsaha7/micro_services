CURR_DIR=$(pwd)
LOG_DIR=${CURR_DIR}/../data/logs
SPRING_BOOT_APP="auth-service.jar"
BIN_DIR=${CURR_DIR}/../dist/target/bin

cd $BIN_DIR
mkdir -p ${LOG_DIR}

start() {
    PORT=${1}
    echo "Starting Spring Boot application..."
    START="nohup java -Dspring.profiles.active=local"
    DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${PORT}" 
    if [[ -z "${PORT}" ]]
    then
        START="${START} -jar"
        echo "start"
    else
        START="${START} ${DEBUG} -jar"
        echo "debug"
    fi
    echo "start" ${START}
    $START ${SPRING_BOOT_APP} > ${LOG_DIR}/"app".log 2>&1&
    echo "Spring Boot application started."
}

stop() {
    echo "Stopping Spring Boot application..."
    PID=$(ps aux | grep "${SPRING_BOOT_APP}" | grep -v grep | awk '{print $2}')

    if [ -n "$PID" ]; then
        kill -9 $PID
        echo "Spring Boot application stopped."
    else
        echo "Spring Boot application is not running."
    fi
}

case "$1" in
    debug)
    	stop
        start 8082
        ;;
    stop)
        stop
        ;;
    *)
        exit 1
        ;;
esac

exit 0
