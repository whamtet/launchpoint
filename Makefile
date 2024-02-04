clean:
	rm -rf target

run:
	clj -M:dev

repl:
	clj -M:dev:nrepl

testx:
	clj -M:test

uberjar:
	clj -T:build all
