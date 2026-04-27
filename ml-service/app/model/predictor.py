import csv
import math
from pathlib import Path
from typing import Optional

import joblib
import pandas as pd
from sklearn.ensemble import RandomForestRegressor

BASE_DIR = Path(__file__).parent.parent.parent
DATA_DIR = BASE_DIR / "data"
MODELS_DIR = BASE_DIR / "models"

DEFAULT_MODEL_PATH = MODELS_DIR / "random_forest.joblib"
DEFAULT_DATA_PATH = DATA_DIR / "registrations.csv"

SURGE_THRESHOLD: float = 82.09


class Predictor:
    """Predictor class for registration surge prediction."""

    def __init__(
        self,
        model_path: Path = DEFAULT_MODEL_PATH,
        data_path: Path = DEFAULT_DATA_PATH,
    ):
        """Initialize the predictor.

        Args:
            model_path: Path to trained model file
            data_path: Path to registration data CSV
        """
        self.model_path = model_path
        self.data_path = data_path
        self.model: Optional[RandomForestRegressor] = None
        self.surge_threshold = SURGE_THRESHOLD

    def load_model(self) -> bool:
        """Load the trained model.

        Returns:
            True if model loaded successfully
        """
        if not self.model_path.exists():
            return False

        self.model = joblib.load(self.model_path)
        return True

    def load_data(self) -> pd.DataFrame:
        """Load historical registration data.

        Returns:
            DataFrame with registration data
        """
        df = pd.read_csv(self.data_path)
        return df.sort_values(["year", "month"]).reset_index(drop=True)

    def engineer_features_for_month(
        self,
        df: pd.DataFrame,
        month: int,
        year: int,
    ) -> pd.DataFrame:
        """Create features for predicting a specific month.

        Args:
            df: Historical data
            month: Month to predict (1-12)
            year: Year to predict

        Returns:
            DataFrame with engineered features
        """
        quarter = ((month - 1) // 3) + 1

        month_sin = math.sin(2 * 3.14159 * month / 12)
        month_cos = math.cos(2 * 3.14159 * month / 12)

        is_winter = 1 if month in [12, 1, 2] else 0
        is_summer = 1 if month in [6, 7, 8] else 0

        last_rows = df.tail(12).copy()

        lag_1 = last_rows.iloc[-1]["registrations"] if len(last_rows) >= 1 else 0
        lag_2 = last_rows.iloc[-2]["registrations"] if len(last_rows) >= 2 else lag_1
        lag_3 = last_rows.iloc[-3]["registrations"] if len(last_rows) >= 3 else lag_2

        rolling_3 = last_rows.tail(3)["registrations"].mean() if len(last_rows) >= 3 else lag_1
        rolling_6 = last_rows.tail(6)["registrations"].mean() if len(last_rows) >= 6 else rolling_3
        rolling_12 = last_rows.tail(12)["registrations"].mean() if len(last_rows) >= 12 else rolling_6

        features = pd.DataFrame([{
            "month": month,
            "quarter": quarter,
            "year": year,
            "month_sin": month_sin,
            "month_cos": month_cos,
            "is_winter": is_winter,
            "is_summer": is_summer,
            "lag_1": lag_1,
            "lag_2": lag_2,
            "lag_3": lag_3,
            "rolling_3": rolling_3,
            "rolling_6": rolling_6,
            "rolling_12": rolling_12,
        }])

        return features

    def predict_next_n_months(
        self,
        n_months: int = 6,
    ) -> list[dict]:
        """Predict registrations for the next N months.

        Args:
            n_months: Number of months to predict

        Returns:
            List of prediction dictionaries
        """
        if self.model is None:
            if not self.load_model():
                raise ValueError("Model not loaded and could not be loaded")

        df = self.load_data()

        last_row = df.iloc[-1]
        current_month = int(last_row["month"])
        current_year = int(last_row["year"])

        predictions = []

        for i in range(n_months):
            predict_month = current_month + i + 1
            predict_year = current_year

            if predict_month > 12:
                predict_month = predict_month - 12
                predict_year = predict_year + 1

            features = self.engineer_features_for_month(
                df, predict_month, predict_year
            )

            prediction = self.model.predict(features)[0]
            prediction = max(0, round(prediction))

            is_surge = prediction >= self.surge_threshold

            month_str = f"{predict_year}-{predict_month:02d}"

            predictions.append({
                "month": month_str,
                "predicted_count": int(prediction),
                "is_surge": is_surge,
            })

            new_row = pd.DataFrame([{
                "month": predict_month,
                "year": predict_year,
                "month_name": "",
                "registrations": prediction,
            }])
            df = pd.concat([df, new_row], ignore_index=True)

        return predictions

    def is_ready(self) -> bool:
        """Check if predictor is ready (model loaded).

        Returns:
            True if model is loaded
        """
        return self.model is not None


def get_predictor() -> Predictor:
    """Get a predictor instance.

    Returns:
        Predictor instance
    """
    return Predictor()
