import csv
import random
from pathlib import Path
from datetime import datetime, timedelta

BASE_DIR = Path(__file__).parent.parent.parent
DATA_DIR = BASE_DIR / "data"


def generate_synthetic_data(
    start_year: int = 2025,
    start_month: int = 5,
    num_months: int = 12,
    noise_factor: float = 0.15,
) -> list[dict]:
    """Generate synthetic monthly registration data with seasonal patterns.

    Seasonal pattern:
    - Winter (Dec, Jan, Feb): High surge ~80-100
    - Summer (Jun, Jul, Aug): Low/relaxed ~30-45
    - Spring/Fall: Moderate ~50-65

    Args:
        start_year: Starting year
        start_month: Starting month (1-12)
        num_months: Number of months to generate
        noise_factor: Random noise factor (0.15 = ±15%)

    Returns:
        List of dictionaries with monthly registration data
    """
    SEASONAL_BASE = {
        1: 90,   # January - Winter surge
        2: 85,   # February - Winter
        3: 65,   # March - Spring
        4: 55,   # April - Spring
        5: 50,   # May - Spring
        6: 40,   # June - Summer start
        7: 35,   # July - Summer low
        8: 38,   # August - Summer
        9: 55,   # September - Fall
        10: 60,  # October - Fall
        11: 70,  # November - Pre-winter
        12: 95,  # December - Winter surge
    }

    data = []
    current_year = start_year
    current_month = start_month

    for _ in range(num_months):
        base = SEASONAL_BASE[current_month]
        noise = random.uniform(-noise_factor, noise_factor)
        count = int(base * (1 + noise))
        count = max(20, min(120, count))

        month_name = datetime(current_year, current_month, 1).strftime("%B")

        data.append({
            "month": current_month,
            "year": current_year,
            "month_name": month_name,
            "registrations": count,
        })

        current_month += 1
        if current_month > 12:
            current_month = 1
            current_year += 1

    return data


def save_to_csv(data: list[dict], filename: str = "registrations.csv") -> Path:
    """Save data to CSV file.

    Args:
        data: List of registration data dictionaries
        filename: Output filename

    Returns:
        Path to saved file
    """
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    filepath = DATA_DIR / filename

    with open(filepath, "w", newline="") as f:
        writer = csv.DictWriter(
            f, fieldnames=["month", "year", "month_name", "registrations"]
        )
        writer.writeheader()
        writer.writerows(data)

    return filepath


def main():
    """Generate and save synthetic registration data."""
    random.seed(42)

    data = generate_synthetic_data(
        start_year=2025,
        start_month=5,
        num_months=12,
        noise_factor=0.15,
    )

    filepath = save_to_csv(data)
    print(f"Generated {len(data)} months of synthetic data")
    print(f"Saved to: {filepath}")

    total = sum(d["registrations"] for d in data)
    avg = total / len(data)
    print(f"Total registrations: {total}")
    print(f"Average per month: {avg:.1f}")


if __name__ == "__main__":
    main()
