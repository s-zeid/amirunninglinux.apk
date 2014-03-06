all: debug
.PHONY: debug release clean

debug:
	./gen-icons || true
	ant debug

release:
	./gen-icons || true
	ant release
	# http://code.s.zeid.me/bin/raw/master/sign-apk
	sign-apk -v "`ls bin/*-release-unsigned.apk | head -n1`"

clean:
	rm -rf bin gen
