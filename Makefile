.PHONY: upload upload-local

upload:
	gradle publishPlugins

upload-local:
	gradle uploadArchives
