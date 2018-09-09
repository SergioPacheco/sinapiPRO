#!/bin/bash
# cd /path/to/my/repo
# git remote add origin https://me@bitbucket.org/me/test.git
git add .
git commit -m "Update"
git push -u origin --all # pushes up the repo and its refs for the first time
git push -u origin --tags # pushes up any tags
