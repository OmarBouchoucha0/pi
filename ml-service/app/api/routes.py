from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from app.model.predictor import Predictor, SURGE_THRESHOLD

router = APIRouter()

predictor = Predictor()


class PredictionRequest(BaseModel):
    n_months: int = 6


class PredictionResponse(BaseModel):
    predictions: list[dict]
    surge_threshold: float = SURGE_THRESHOLD
    model_version: str = "1.0.0"


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool


@router.get("/health", response_model=HealthResponse)
def health_check():
    """Check if the service and model are ready."""
    model_loaded = predictor.load_model()
    return HealthResponse(
        status="ok" if model_loaded else "error",
        model_loaded=model_loaded,
    )


@router.post("/predict", response_model=PredictionResponse)
def predict(request: PredictionRequest = PredictionRequest()):
    """Predict registrations for the next N months.

    Args:
        request: Prediction request with n_months

    Returns:
        Predictions for the next N months
    """
    if not predictor.load_model():
        raise HTTPException(status_code=500, detail="Model not loaded")

    try:
        predictions = predictor.predict_next_n_months(n_months=request.n_months)
        return PredictionResponse(
            predictions=predictions,
            surge_threshold=SURGE_THRESHOLD,
            model_version="1.0.0",
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/data")
def get_data():
    """Get historical registration data."""
    try:
        df = predictor.load_data()
        return df.to_dict(orient="records")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/surge")
def get_surge_predictions():
    """Get only surge predictions."""
    if not predictor.load_model():
        raise HTTPException(status_code=500, detail="Model not loaded")

    try:
        all_predictions = predictor.predict_next_n_months(n_months=6)
        surge_predictions = [
            p for p in all_predictions if p["is_surge"]
        ]
        return {
            "surge_predictions": surge_predictions,
            "surge_threshold": SURGE_THRESHOLD,
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
