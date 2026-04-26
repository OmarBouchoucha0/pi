# Registration Surge Prediction Service

ML service for predicting user registration surges using Random Forest Regressor.

## Setup

```bash
# Create virtual environment
python -m venv venv

# Activate
source venv/bin/activate  # bash/zsh
# OR
venv\Scripts\Activate.ps1  # PowerShell

# Install dependencies
pip install -r requirements.txt

# Generate data (already done)
python app/data/synthetic.py

# Train model (already done)
python app/model/trainer.py
```

## Running

```bash
# Activate virtual environment first
source venv/bin/activate

# Run the API
python -m uvicorn app.main:app --reload --port 8000
```

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Service info |
| `/api/health` | GET | Check if model loaded |
| `/api/predict` | POST | Predict next N months |
| `/api/data` | GET | Get historical data |
| `/api/surge` | GET | Get only surge predictions |

## Example Usage

```bash
# Health check
curl http://localhost:8000/api/health

# Predict next 6 months
curl -X POST http://localhost:8000/api/predict -H "Content-Type: application/json" -d '{"n_months": 6}'
```

## Files

```
ml-service/
├── app/
│   ├── main.py              # FastAPI app
│   ├── model/
│   │   ├── trainer.py      # Model training
│   │   └── predictor.py  # Prediction logic
│   ├── data/
│   │   └── synthetic.py  # Data generator
│   └── api/
│       └── routes.py     # API routes
├── models/
│   └── random_forest.joblib  # Trained model
├── data/
│   └── registrations.csv     # Training data
├── requirements.txt
└── README.md
```
