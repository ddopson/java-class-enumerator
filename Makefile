ECHO     ?= echo  # MacOS compat: using abs path for 'echo' breaks. Alternatively, this gets fixed when using bash as the shell (vs 'sh')
ECHO_E   ?= $(ECHO) $(shell $(ECHO) -e foo | perl -ne '/^foo/ and print "-e"')

GREEN   = \033[32m
YELLOW  = \033[38;5;226m
NOCOLOR = \033[39;0m


java_src_files = $(shell find src -name '*.java')

.PHONY: default
default: clean build jar test

.PHONY: clean
clean:
	@$(ECHO_E) "$(YELLOW) Cleaning...$(NOCOLOR)"
	rm -rf build/

.PHONY: build
build:
	@$(ECHO_E) "$(YELLOW) Building...$(NOCOLOR)"
	@mkdir -p build/classes
	javac -d build/classes $(java_src_files)

.PHONY: jar
jar:
	@$(ECHO_E) "$(YELLOW) Making JAR Files...$(NOCOLOR)"
	jar cf build/ClassEnumerator_test.jar -C build/classes/ . 
	jar cf build/ClassEnumerator.jar -C build/classes/ pro

.PHONY: test
test:	
	@$(ECHO_E) "$(YELLOW) Running Filesystem Classpath Test...$(NOCOLOR)"
	java -classpath build/classes test.TestClassEnumeration
	java -classpath build/classes test.TestClassEnumerationFindAll
	@$(ECHO_E) "$(YELLOW) Running JAR Classpath Test...$(NOCOLOR)"
	java -classpath build/ClassEnumerator_test.jar  test.TestClassEnumeration
	@$(ECHO_E) "$(YELLOW) Pass. $(NOCOLOR)"
	@$(ECHO_E) "$(YELLOW) Running JAR Classpath second Test...$(NOCOLOR)"
	java -classpath build/ClassEnumerator_test.jar  test.TestClassEnumerationFindAll
	@$(ECHO_E) "$(YELLOW) Pass. $(NOCOLOR)"
