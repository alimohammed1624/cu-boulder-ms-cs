while [ true ]; do for i in {1..10}; do curl -v -H "Accept: application/json" http://localhost:8881/articles; done; sleep 5; done
