# Copyright © 2020, Concordant and contributors.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
# associated documentation files (the "Software"), to deal in the Software without restriction,
# including without limitation the rights to use, copy, modify, merge, publish, distribute,
# sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all copies or
# substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
# NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
# DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
# OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
	
all: build test doc deploy

build: build-gradle build-ts

build-gradle:
	gradle assemble

build-ts: build-gradle
	mkdir -p build/ts
	java -jar build/libs/c-crdtlib-jvm.jar > build/ts/c-crdtlib.d.ts

test: build
	gradle allTests

VERSION=0.0.6
DESCRIPTION=Concordant Conflict-Free Replicated Datatypes (CRDT) library
LICENSE=MIT
AUTHOR={\"name\": \"Ludovic Le Frioux\", \"email\": \"ludovic.lefrioux@concordant.io\"}
REPOSITORY={\n    \"type\": \"git\",\n    \"url\": \"git+ssh:\/\/git@gitlab.inria.fr\/concordant\/software\/c-crdtlib.git\"\n  }
ISSUES={\n    \"url\": \"https:\/\/gitlab.inria.fr\/concordant\/software\/c-crdtlib\/-\/issues\"\n  }

deploy: build
	# Create deploy directory
	rm -rf deploy
	mkdir deploy
	mkdir deploy/npm
	mkdir deploy/jvm
	
	# Rename the Node.js generated package
	cp -r build/js/packages/c-crdtlib-nodeJs deploy/npm/c-crdtlib
	# Remove useless files
	rm deploy/npm/c-crdtlib/package.json.hash
	rm deploy/npm/c-crdtlib/kotlin/c-crdtlib-nodeJs.meta.js
	rm -rf deploy/npm/c-crdtlib/kotlin/c-crdtlib-nodeJs
	# Rename files
	mv deploy/npm/c-crdtlib/kotlin/c-crdtlib-nodeJs.js deploy/npm/c-crdtlib/kotlin/c-crdtlib.js
	mv deploy/npm/c-crdtlib/kotlin/c-crdtlib-nodeJs.js.map deploy/npm/c-crdtlib/kotlin/c-crdtlib.js.map
	mv deploy/npm/c-crdtlib/kotlin deploy/npm/c-crdtlib/lib
	# Modify source with correct package name
	perl -i -pe "s/-nodeJs//g" deploy/npm/c-crdtlib/lib/c-crdtlib.js
	perl -i -pe "s/-nodeJs//g" deploy/npm/c-crdtlib/lib/c-crdtlib.js.map
	# Add @types for npm package
	cp build/ts/c-crdtlib.d.ts deploy/npm/c-crdtlib/lib
	# Modify package.json
	perl -i -pe "s/-nodeJs//g" deploy/npm/c-crdtlib/package.json
	perl -i -pe "s/kotlin\//lib\//g" deploy/npm/c-crdtlib/package.json
	perl -i -ne "print unless /\"name\"/" deploy/npm/c-crdtlib/package.json
	perl -i -ne "print unless /\"version\"/" deploy/npm/c-crdtlib/package.json
	perl -i -pe "s/\[\],/[]/g" deploy/npm/c-crdtlib/package.json
	perl -i -pe "s/^{$$/{\n  \"name\": \"c-crdtlib\",\n  \"version\": \"$(VERSION)\",\n  \"description\": \"$(DESCRIPTION)\",\n  \"license\": \"$(LICENSE)\",\n  \"author\": $(AUTHOR),\n  \"repository\": $(REPOSITORY),\n  \"bugs\": $(ISSUES),\n  \"private\": true,/g" deploy/npm/c-crdtlib/package.json
	perl -i -pe "s/c-crdtlib\.js\",/c-crdtlib.js\",\n  \"types\": \"lib\/c-crdtlib.d.ts\",/g" deploy/npm/c-crdtlib/package.json
	# Add license file to the npm package
	cp LICENSE deploy/npm/c-crdtlib
	# Pack the npm package
	cd deploy/npm/c-crdtlib; npm pack
	mv deploy/npm/c-crdtlib/c-crdtlib*.tgz deploy/npm
	
	# Deploy jvm jar
	cp build/libs/c-crdtlib-jvm.jar deploy/jvm/c-crdtlib.jar

doc:
	gradle dokka

clean:
	rm -rf build deploy