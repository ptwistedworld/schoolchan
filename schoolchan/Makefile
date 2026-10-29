JAVA_HOME ?= ../tools/jdk-21
export JAVA_HOME
PATH := $(JAVA_HOME)/bin:$(PATH)

.PHONY: up down run run-ml run-ml-real test seed lint eval demo models setup-real backup verify-postgres

up:
	docker compose up -d postgres

down:
	docker compose down

run:
	cd backend && ./gradlew bootRun --args='--spring.profiles.active=dev'

run-ml:
	cd ml-service && MOCK_MODE=true uvicorn app.main:app --host 0.0.0.0 --port 8090

# The real moderation models. Needs Python 3.11 or 3.12; run `make setup-real` first.
run-ml-real:
	cd ml-service && MOCK_MODE=false TEXT_MODEL_ID=vectorsense/toxiscan \
		IMAGE_MODEL_ID=onnx-community/nsfw_image_detection-ONNX \
		TRANSLATE_MODEL_ID=facebook/nllb-200-distilled-600M \
		uvicorn app.main:app --host 0.0.0.0 --port 8090

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

# Create the environment for the real models (needs Python 3.11 or 3.12).
setup-real:
	./scripts/setup-real-models.ps1

# Check that every configured model id really exists and the files are there.
models:
	cd ml-service && python scripts/verify_models.py

# Copy the local database so a bad run cannot lose your data.
backup:
	./scripts/backup-db.ps1

# Run the app against PostgreSQL once, to prove the Flyway migrations work.
verify-postgres:
	docker compose up -d postgres
	cd backend && ./gradlew bootRun --args='--spring.profiles.active=dev'
