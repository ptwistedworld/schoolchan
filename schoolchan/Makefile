JAVA_HOME ?= ../tools/jdk-21
export JAVA_HOME
PATH := $(JAVA_HOME)/bin:$(PATH)

.PHONY: up down run run-ml test seed lint eval demo

up:
	docker compose up -d postgres

down:
	docker compose down

run:
	cd backend && ./gradlew bootRun --args='--spring.profiles.active=dev'

run-ml:
	cd ml-service && MOCK_MODE=true uvicorn app.main:app --host 0.0.0.0 --port 8090

test:
	cd backend && ./gradlew test
	cd ml-service && python -m pytest -q || true

seed:
	cd backend && ./gradlew bootRun --args='--spring.profiles.active=dev,seed --schoolchan.seed=true'

lint:
	cd backend && ./gradlew check

eval:
	cd backend && ./gradlew bootRun --args='--spring.profiles.active=local --schoolchan.eval.run=true'

demo:
	$(MAKE) seed
