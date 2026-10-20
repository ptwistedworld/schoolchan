"""API tests for SchoolChan ML sidecar (mock mode)."""

import io

import pytest
from fastapi.testclient import TestClient
from PIL import Image

from app.main import app

client = TestClient(app)


def test_health_ok():
    response = client.get("/health")
    assert response.status_code == 200
    body = response.json()
    assert body["status"] == "ok"
    assert body["mock_mode"] is True
    assert "services" in body


def test_text_classify_baseline():
    response = client.post(
        "/v1/text/classify",
        json={"text": "Hello SchoolChan student message."},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["toxicity"] == pytest.approx(0.02)
    assert data["threat"] == pytest.approx(0.02)
    assert data["model_id"] == "schoolchan-text-mock-v1"


def test_text_classify_token_toxic():
    response = client.post(
        "/v1/text/classify",
        json={"text": "Review TOKEN_TOXIC placeholder for moderation."},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["toxicity"] >= 0.9
    assert data["hate"] == pytest.approx(0.02)


def test_text_classify_token_threat():
    response = client.post(
        "/v1/text/classify",
        json={"text": "Flag TOKEN_THREAT in pipeline test."},
    )
    data = response.json()
    assert data["threat"] >= 0.9


def test_translate_english_passthrough():
    response = client.post(
        "/v1/translate",
        json={"text": "Good morning, class."},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["translated_text"] == "Good morning, class."
    assert data["detected_language"] == "en"


def test_translate_tagalog_stub():
    response = client.post(
        "/v1/translate",
        json={"text": "Kumusta po, maganda nga ang araw."},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["detected_language"] == "tl"
    assert "stub English translation" in data["translated_text"]


def _tiny_png_bytes() -> bytes:
    buf = io.BytesIO()
    Image.new("RGB", (8, 8), color=(128, 128, 128)).save(buf, format="PNG")
    return buf.getvalue()


def test_image_classify_safe_mock():
    response = client.post(
        "/v1/image/classify",
        files={"file": ("photo.png", _tiny_png_bytes(), "image/png")},
    )
    assert response.status_code == 200
    data = response.json()
    assert data["nsfw_score"] == pytest.approx(0.01)


def test_image_classify_nsfw_token_in_filename():
    response = client.post(
        "/v1/image/classify",
        files={"file": ("TOKEN_NSFW_sample.png", _tiny_png_bytes(), "image/png")},
    )
    data = response.json()
    assert data["nsfw_score"] >= 0.85


def test_image_classify_nsfw_token_in_path_hint():
    response = client.post(
        "/v1/image/classify?path_hint=/uploads/TOKEN_NSFW/archive.png",
        files={"file": ("x.png", _tiny_png_bytes(), "image/png")},
    )
    data = response.json()
    assert data["nsfw_score"] >= 0.85
