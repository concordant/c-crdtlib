pull update: link
	# svn update
	git pull

push commit: link
	# svn commit -m ''
	git add Makefile
	git add -u
	git commit --allow-empty-message -m ""
	git push origin master

link:
	# # ln -s ~/bin/svnadd ./add
	# ln -sf ~/bin/gitadd ./add
