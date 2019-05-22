all: build

build:
	@docker build --tag=jplf/openfire .

release: build
	@docker build --tag=sameersbn/openfire:$(shell cat VERSION) .
