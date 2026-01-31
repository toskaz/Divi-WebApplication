import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 5 }, 
    { duration: '30s', target: 20 },
    { duration: '10s', target: 0 },  
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
    http_req_failed: ['rate<0.01'],   // Error rate must be under 1%
  },
};

const BASE_URL = 'http://localhost:8080'; 

const USER_CREDENTIALS = {
  email: 'matistan05@gmail.com', 
  password: 'password',
};

export function setup() {
  const loginRes = http.post(
    `${BASE_URL}/api/auth/login`, 
    JSON.stringify(USER_CREDENTIALS), 
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (loginRes.status !== 200) {
    console.error(`Login failed: ${loginRes.body}`);
  }

  check(loginRes, {
    'Login successful': (r) => r.status === 200,
  });

  return { token: loginRes.json('token') };
}

export default function (data) {
  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${data.token}`, 
    },
  };

  group('User Session', function () {

    const resGroups = http.get(`${BASE_URL}/api/groups/me`, params);
    
    check(resGroups, {
      'Get Groups 200': (r) => r.status === 200,
      'Fast response': (r) => r.timings.duration < 300,
    });

    sleep(1); 
    const resCurrencies = http.get(`${BASE_URL}/api/currencies`, params);
    
    check(resCurrencies, {
      'Get Currencies 200': (r) => r.status === 200,
    });
  });

  sleep(1);
}