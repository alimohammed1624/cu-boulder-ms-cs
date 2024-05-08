import http from 'k6/http';
import {check} from 'k6';

export const options = {
	stages: [
		{ duration: '30s', target: 10 },
	]
};

export default function () {
	const url = 'http://localhost:8081/request-registration';
	const payload = JSON.stringify({
		"email": "test@example.com"
	});
	
	const params = {
		headers: {
			'Content-Type': 'application/json',
		},
	};
	
	const res = http.post(url, payload, params);
	check(res, {
		'is status 200': (r) => r.status === 204,
	});
}
