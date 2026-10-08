import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        burst_test: {
            executor: 'constant-arrival-rate',
            rate: 1000,
            timeUnit: '1s',
            duration: '5s',

            preAllocatedVUs: 100,
            maxVUs: 500,
        },
    },
};

export default function () {
    const res = http.get('http://localhost/api/products', {
        headers: {
            'X-API-KEY': 'load-test',
        },
    });

    check(res, {
        'request allowed': (r) => r.status === 200,
        'request rejected': (r) => r.status === 429,
    });
}