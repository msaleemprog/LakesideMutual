Write-Host "== Smoke: Backends =="

function Check-Url($url, $name) {
    try {
        Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5 | Out-Null
        Write-Host "✅ $name"
    } catch {
        Write-Host "❌ $name"
        exit 1
    }
}

Check-Url "http://localhost:8080/actuator/health" "self-service-backend (8080)"
Check-Url "http://localhost:8090/actuator/health" "policy-management-backend (8090)"
Check-Url "http://localhost:8100/actuator/health" "customer-management-backend (8100)"
Check-Url "http://localhost:8110/actuator/health" "customer-core (8110)"

Write-Host "== Smoke: Frontends =="

Check-Url "http://localhost:3000" "self-service-frontend (3000)"
Check-Url "http://localhost:3010" "policy-frontend (3010)"
Check-Url "http://localhost:3020" "customer-management-frontend (3020)"

Write-Host "✅ All smoke checks passed"
