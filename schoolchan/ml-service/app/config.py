import os
from functools import lru_cache


def _env_bool(name: str, default: bool) -> bool:
    raw = os.getenv(name)
    if raw is None:
        return default
    return raw.strip().lower() in ("1", "true", "yes", "on")


@lru_cache
def get_settings() -> "Settings":
    return Settings()


class Settings:
    """Runtime configuration from environment variables."""

    mock_mode: bool
    text_model_id: str
    image_model_id: str
    translate_model_id: str

    def __init__(self) -> None:
        self.mock_mode = _env_bool("MOCK_MODE", True)
        self.text_model_id = os.getenv("TEXT_MODEL_ID", "schoolchan-text-mock-v1")
        self.image_model_id = os.getenv("IMAGE_MODEL_ID", "schoolchan-image-mock-v1")
        self.translate_model_id = os.getenv(
            "TRANSLATE_MODEL_ID", "schoolchan-translate-mock-v1"
        )
