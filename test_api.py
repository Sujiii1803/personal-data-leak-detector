import requests
import json
import sys

base_url = 'http://localhost:8080/api'
email = 'testocr@example.com'
password = 'password123'

# 1. Register
print("Registering user...")
res = requests.post(f"{base_url}/auth/register", json={"email": email, "password": password})
if res.status_code not in [200, 201]:
    if "already registered" not in res.text and "already exists" not in res.text:
        print(f"Register failed: {res.status_code} - {res.text}")
        sys.exit(1)

# 2. Login
print("Logging in...")
res = requests.post(f"{base_url}/auth/login", json={"email": email, "password": password})
if res.status_code != 200:
    print(f"Login failed: {res.status_code} - {res.text}")
    sys.exit(1)

token = res.json().get('token')
headers = {'Authorization': f'Bearer {token}'}

# 3. Upload image
print("Uploading image for scanning...")
with open("aadhaar_test.png", "rb") as f:
    files = {'file': ('aadhaar_test.png', f, 'image/png')}
    res = requests.post(f"{base_url}/scan/file", headers=headers, files=files)

print(f"Response status: {res.status_code}")
try:
    data = res.json()
    print(json.dumps(data, indent=2))
except:
    print(res.text)
