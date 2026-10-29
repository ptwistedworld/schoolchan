"""Shared helpers for loading real ONNX models without PyTorch.

The text and image models are ONNX graphs, so we only need
``onnxruntime`` plus the Hugging Face tokenizer / image processor. No
PyTorch is required, which keeps the download small and CPU-only.
"""

from __future__ import annotations

import json
import os
from pathlib import Path
from typing import Any


def model_cache_dir() -> Path:
    """Where the downloaded weights live. Offline after the first download."""
    root = os.getenv("ML_MODEL_CACHE", "./models")
    path = Path(root).expanduser().resolve()
    path.mkdir(parents=True, exist_ok=True)
    return path


def download_repo(repo_id: str, files: list[str] | None = None) -> Path:
    """Download a model repo into the cache and return the local folder."""
    from huggingface_hub import snapshot_download

    target = model_cache_dir() / repo_id.replace("/", "_")
    if target.exists() and any(target.iterdir()):
        return target
    snapshot_download(
        repo_id,
        local_dir=str(target),
        allow_patterns=files,
        cache_dir=str(model_cache_dir() / "_hub"),
    )
    return target


def load_labels(path: Path) -> dict[int, str]:
    """Read labels.json, which may be a list or an id -> label map."""
    data = json.loads(path.read_text(encoding="utf-8"))
    if isinstance(data, list):
        return {i: str(label) for i, label in enumerate(data)}
    return {int(k): str(v) for k, v in data.items()}


def sigmoid(x: float) -> float:
    if x >= 0:
        return 1.0 / (1.0 + pow(2.718281828459045, -x))
    exp = pow(2.718281828459045, x)
    return exp / (1.0 + exp)


def softmax(values: list[float]) -> list[float]:
    if not values:
        return []
    m = max(values)
    exps = [pow(2.718281828459045, v - m) for v in values]
    total = sum(exps)
    return [e / total for e in exps]


def make_session(model_path: Path, providers: list[str] | None = None) -> Any:
    """Create an onnxruntime session on the CPU."""
    import onnxruntime as ort

    available = ort.get_available_providers()
    chosen = [p for p in (providers or ["CPUExecutionProvider"]) if p in available]
    if not chosen:
        chosen = [available[0]]
    opts = ort.SessionOptions()
    opts.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_ALL
    return ort.InferenceSession(str(model_path), sess_options=opts, providers=chosen)
