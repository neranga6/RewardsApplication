import json
import sys
import time
from typing import Any, Dict

import requests

BASE_URL = "http://localhost:8080"
REPORT_FILE = "smoke-test-report.json"


def record_result(name: str, success: bool, details: Any) -> Dict[str, Any]:
    return {
        "name": name,
        "success": success,
        "details": details,
    }


def main() -> int:
    results = []

    try:
        health = requests.get(f"{BASE_URL}/actuator/health", timeout=10)
        health.raise_for_status()
        health_json = health.json()
        results.append(
            record_result(
                "health_check",
                health_json.get("status") == "UP",
                health_json,
            )
        )
    except Exception as exc:
        results.append(record_result("health_check", False, str(exc)))

    try:
        rewards = requests.get(f"{BASE_URL}/api/rewards", timeout=10)
        rewards.raise_for_status()
        rewards_json = rewards.json()
        results.append(
            record_result(
                "get_all_rewards",
                isinstance(rewards_json, list),
                rewards_json,
            )
        )
    except Exception as exc:
        results.append(record_result("get_all_rewards", False, str(exc)))

    payload = {
        "id": 999,
        "customer_id": 500,
        "customer_name": "GitHub Action User",
        "amount": 120,
        "transaction_date": "2026-03-26"
    }

    try:
        create_txn = requests.post(
            f"{BASE_URL}/api/rewards/transactions",
            json=payload,
            timeout=10,
        )
        create_txn.raise_for_status()
        txn_json = create_txn.json()
        results.append(
            record_result(
                "create_transaction",
                txn_json.get("customerName") == "GitHub Action User",
                txn_json,
            )
        )
    except Exception as exc:
        results.append(record_result("create_transaction", False, str(exc)))

    overall_success = all(item["success"] for item in results)

    report = {
        "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
        "overall_success": overall_success,
        "results": results,
    }

    with open(REPORT_FILE, "w", encoding="utf-8") as f:
        json.dump(report, f, indent=2)

    print(json.dumps(report, indent=2))

    return 0 if overall_success else 1


if __name__ == "__main__":
    sys.exit(main())
