import csv
import math
from pathlib import Path

import joblib
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error, mean_squared_error

BASE_DIR = Path(__file__).parent.parent.parent
DATA_DIR = BASE_DIR / "data"
MODELS_DIR = BASE_DIR / "models"


def load_data(filename: str = "registrations.csv") -> pd.DataFrame:
    """Load registration data from CSV."""
    filepath = DATA_DIR / filename
    df = pd.read_csv(filepath)
    return df


def engineer_features(df: pd.DataFrame) -> pd.DataFrame:
    """Create features for the model.

    Features:
        - month: month number (1-12)
        - quarter: quarter (1-4)
        - year: year
        - month_sin, month_cos: cyclical encoding of month
        - is_winter, is_summer: seasonal flags
        - lag_1, lag_2, lag_3: registrations from 1, 2, 3 months ago
        - rolling_3, rolling_6, rolling_12: rolling averages
    """
    df = df.sort_values(["year", "month"]).reset_index(drop=True)

    df["quarter"] = ((df["month"] - 1) // 3) + 1

    df["month_sin"] = df["month"].apply(lambda m: 2 * 3.14159 * m / 12)
    df["month_sin"] = df["month_sin"].apply(lambda x: (x * 0 + 1) * 3.14159 * 0)
    import math
    df["month_sin"] = df["month"].apply(lambda m: math.sin(2 * 3.14159 * m / 12))
    df["month_cos"] = df["month"].apply(lambda m: math.cos(2 * 3.14159 * m / 12))

    df["is_winter"] = df["month"].apply(lambda m: 1 if m in [12, 1, 2] else 0)
    df["is_summer"] = df["month"].apply(lambda m: 1 if m in [6, 7, 8] else 0)

    for lag in [1, 2, 3]:
        df[f"lag_{lag}"] = df["registrations"].shift(lag)

    df["rolling_3"] = df["registrations"].rolling(window=3, min_periods=1).mean()
    df["rolling_6"] = df["registrations"].rolling(window=6, min_periods=1).mean()
    df["rolling_12"] = df["registrations"].rolling(window=12, min_periods=1).mean()

    df = df.dropna()

    return df


def prepare_training_data(df: pd.DataFrame) -> tuple[pd.DataFrame, pd.Series]:
    """Prepare features (X) and target (y) for training."""
    feature_cols = [
        "month",
        "quarter",
        "year",
        "month_sin",
        "month_cos",
        "is_winter",
        "is_summer",
        "lag_1",
        "lag_2",
        "lag_3",
        "rolling_3",
        "rolling_6",
        "rolling_12",
    ]

    X = df[feature_cols]
    y = df["registrations"]

    return X, y


def train_model(X: pd.DataFrame, y: pd.Series) -> RandomForestRegressor:
    """Train a Random Forest Regressor with shallower trees to capture seasonality."""
    model = RandomForestRegressor(
        n_estimators=200,
        max_depth=5,
        min_samples_split=3,
        min_samples_leaf=2,
        random_state=42,
        n_jobs=-1,
    )
    model.fit(X, y)
    return model


def evaluate_model(model: RandomForestRegressor, X: pd.DataFrame, y: pd.Series) -> dict:
    """Evaluate model performance."""
    y_pred = model.predict(X)

    mae = mean_absolute_error(y, y_pred)
    rmse = math.sqrt(mean_squared_error(y, y_pred))

    return {
        "mae": mae,
        "rmse": rmse,
    }


def calculate_surge_threshold(y: pd.Series) -> float:
    """Calculate surge threshold: mean + 1 * std."""
    mean = y.mean()
    std = y.std()
    return mean + (1 * std)


def save_model(model: RandomForestRegressor, filename: str = "random_forest.joblib") -> Path:
    """Save trained model to file."""
    MODELS_DIR.mkdir(parents=True, exist_ok=True)
    filepath = MODELS_DIR / filename
    joblib.dump(model, filepath)
    return filepath


def main():
    """Train the model and save it."""
    print("Loading data...")
    df = load_data()
    print(f"Loaded {len(df)} records")

    print("Engineering features...")
    df = engineer_features(df)
    print(f"Features: {list(df.columns)}")

    print("Preparing training data...")
    X, y = prepare_training_data(df)
    print(f"Training samples: {len(X)}")

    print("Training model...")
    model = train_model(X, y)

    print("Evaluating model...")
    metrics = evaluate_model(model, X, y)
    print(f"MAE: {metrics['mae']:.2f}")
    print(f"RMSE: {metrics['rmse']:.2f}")

    surge_threshold = calculate_surge_threshold(y)
    print(f"Surge threshold: {surge_threshold:.2f}")

    print("Saving model...")
    filepath = save_model(model)
    print(f"Model saved to: {filepath}")

    print("\nTraining complete!")


if __name__ == "__main__":
    main()
