DATAFLOW_VERSION=2.3.0.RELEASE SKIPPER_VERSION=2.2.1.RELEASE docker-compose -f ./docker-compose.yml -f ./docker-compose-rabbitmq.yml up

java -jar spring-cloud-dataflow-shell-2.3.0.RELEASE.jar