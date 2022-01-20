h help default:
	@echo "Use : make [action]" \
		&& echo "Actions:" \
		&& echo " → javadoc (alias j, jd): export the javadoc" \
		&& echo " → build (alias b): build the app" \
		&& echo " → run (alias r): run the app" \
		&& echo " → test (alias t, tests): lauch tests" \
		&& echo " → deploy (alias d, jar): generate jar" \
		&& echo " → open_javadoc (alias oj, ojd, open_jd): open javadoc if existing" \
		&& echo " → javadoc_open (alias jo, jdo, jd_open): generate and open javadoc"
		

j jd javadoc:
	@echo "Exporting javadoc build/docs/javadoc folder..." \
		&& gradle javadoc

b build:
	@echo "Building..." \
		&& gradle build

r run:
	@echo "Running..." \
		&& gradle run

t test tests:
	@echo "Running tests..." \
		&& gradle test

d deploy jar:
	@echo "Generating jar in build/libs folder..." \
		&& gradle jar

oj ojd open_jd open_javadoc:
	@echo "Opening javadoc..." \
		&& firefox ./build/docs/javadoc/index.html

jo jdo jd_open javadoc_open: j oj