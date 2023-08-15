// Simple k6 load test
// Run once: $ k6 run simple.js
// Vary a load: k6 run --vus 10 --duration 30s simple.js
//
import http from 'k6/http';
import { sleep } from 'k6';



export const options = {
    discardResponseBodies: true,
    scenarios: {
        happytest: {
            executor: 'constant-arrival-rate',
            rate: 1,
            timeUnit: '5s',
            exec: 'happypath',
            preAllocatedVUs: 1,
            maxVUs: 1,
            duration: '10m',
        },
    },
};

export function happypath() {
    http.get('http://localhost');
}


