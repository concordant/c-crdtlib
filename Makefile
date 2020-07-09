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

build:
	gradle assemble
	mkdir build/ts
	java -jar build/libs/c-crdtlib-jvm.jar > build/ts/c-crdtlib.ts

test: build
	gradle allTests

VERSION=0.0.1
LICENSE=MIT
AUTHOR={\"name\": \"Ludovic Le Frioux\", \"email\": \"ludovic.lefrioux@concordant.io\"}
REPOSITORY={\n    \"type\": \"git\",\n    \"url\": \"git+ssh:\/\/git@gitlab.inria.fr\/concordant\/software\/c-crdtlib.git\"\n  }
ISSUES={\n    \"url\": \"https:\/\/gitlab.inria.fr\/concordant\/software\/c-crdtlib\/-\/issues\"\n  }

deploy: build
	# Create deploy directory
	rm -rf deploy
	mkdir deploy
	mkdir deploy/npm
	
	# Rename the Node.js generated package
	cp -r build/js/packages/c-crdtlib-nodeJs deploy/npm/c-crdtlib
	
	# Remove useless files
	rm deploy/npm/c-crdtlib/package.json.hash
	rm deploy/npm/c-crdtlib/kotlin/c-crdtlib-nodeJs.meta.js
	rm -rf deploy/npm/c-crdtlib/kotlin/c-crdtlib-nodeJs
	
	# Rename files
	mv deploy/npm/c-crdtlib/kotlin/c-crdtlib-nodeJs.js deploy/npm/c-crdtlib/kotlin/c-crdtlib.js
	mv deploy/npm/c-crdtlib/kotlin/c-crdtlib-nodeJs.js.map deploy/npm/c-crdtlib/kotlin/c-crdtlib.js.map
	mv deploy/npm/c-crdtlib/kotlin deploy/npm/c-crdtlib/src
	
	# Modify source with correct package name
	sed -i "s/-nodeJs//g" deploy/npm/c-crdtlib/src/c-crdtlib.js
	sed -i "s/-nodeJs//g" deploy/npm/c-crdtlib/src/c-crdtlib.js.map
	
	# Modify package.json
	sed -i "s/-nodeJs//g" deploy/npm/c-crdtlib/package.json
	sed -i "s/kotlin\//src\//g" deploy/npm/c-crdtlib/package.json
	sed -i "/\"name\"/d" deploy/npm/c-crdtlib/package.json
	sed -i "/\"version\"/d" deploy/npm/c-crdtlib/package.json
	sed -i "s/\[\],/[]/g" deploy/npm/c-crdtlib/package.json
	sed -i "s/^{$$/{\n  \"name\": \"c-crdtlib\",\n  \"version\": \"$(VERSION)\",\n  \"license\": \"$(LICENSE)\",\n  \"author\": $(AUTHOR),\n  \"repository\": $(REPOSITORY),\n  \"bugs\": $(ISSUES),\n  \"private\": true,/g" deploy/npm/c-crdtlib/package.json
	
	# Add license file to the npm package
	cp LICENSE deploy/npm/c-crdtlib
	
	# Pack the npm package
	cd deploy/npm/c-crdtlib; npm pack
	mv deploy/npm/c-crdtlib/c-crdtlib*.tgz deploy/npm

doc:
	gradle dokka

clean:
	rm -rf build deploy
