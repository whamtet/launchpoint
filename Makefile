clean:
	rm -rf target

run:
	clj -M:dev

repl:
	clj -M:dev:nrepl

testx:
	clj -M:test

uberjar:
	pkill java && npm run tailwind && clj -T:build all

uberjarlight:
	npm run tailwind && clj -T:build all
