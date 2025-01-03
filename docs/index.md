# Welcome to my implementation of git

I've been using Git for quite some time, and I thought it would be fun to gain a deeper understanding of how it works by attempting to rebuild it based on my interpretation of its functionality.

## Introduction

This project is a personal implementation of Git, the popular version control system. It’s written in Scala and aims to provide similar functionality to Git while exploring the underlying principles of how version control systems work.

### Commands implemented:
* `init <directory>` - Creates a new git repository. [(git man page)](https://git-scm.com/docs/git-init)
* `add <files>` - Adds a file to the staging area. [(git man page)](https://git-scm.com/docs/git-add)
* `commit <message>` - Record changes to the repository. [(git man page)](https://git-scm.com/docs/git-commit)
* `status` - Show the working tree status of the repository. [(git man page)](https://git-scm.com/docs/git-status)
* `log` - Show all commits made. [(git man page)](https://git-scm.com/docs/git-log)
* `restore <files>` - Restore working tree files. [(git man page)](https://git-scm.com/docs/git-restore)
* `restore--staged <files>` - Restore content in the index. [(git man page)](https://git-scm.com/docs/git-restore)
* `checkout <hash>` - Restore working tree files to the given hash. [(git man page)](https://git-scm.com/docs/git-checkout)
* `rm <files>` - Remove files from the worktree and index. [(git man page)](https://git-scm.com/docs/git-rm)
* `pls-work` - A desperate plea to the version control gods. Sometimes, you just need a little extra luck.

## Project layout

### Packages

The `main.scala` file has code to parse the command line arguments and call the respective "bridge functions" located in separate scala files. There are three types of packages:

    package gitcommands # has code for individual git commands
    package datastructs # has code for the data structures I've made
    package fileops # has helper functions related to file operations

A wegit repo looks like this:

    .wegit/
        INDEX
        COMMIT
        objs/
            e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855  # a blob
            ...       # more blobs


All file objects are represented by a SHA hash. The contents of the file are compressed and stored in the `objs/` directory, where the name of the file is its SHA hash.

### Data Structures

An INDEX data structure and a COMMIT data structure are used to represent the current state of the repository.

The index looks like a `String => (String, String)` map. The key is the path of the file, and the tuple represents the `old` and `new` SHA hashes of the file. The `old` hash is the state of the file from the latest commit (or `null` if it didn't exist) and the `new` hash is the state of the file from the latest `git add` operation.

For example, an index file would look like this:
```plaintext
    1.txt:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855:2860d7deca71859f9fae69da862b3934b772f24d23137f326a030bf042dc8d7d
    2.txt:0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1:0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1
    3.txt:null:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    egfolder\4.txt:null:e4ec861d34ab491b5b8538ee693c70ccda97e2ac861b8719b0b1d2ffdfe69cd7
```
A commit is just a snapshot of an index file with an additional commit message, and a unique commit hash that is generated. It would look like this:

```plaintext
    [da563b40a3e49923b50a173b0cc73c40627a0430]
    Second commit
    1.txt=e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855,2860d7deca71859f9fae69da862b3934b772f24d23137f326a030bf042dc8d7d
    2.txt=0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1,0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1
    3.txt=null,e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    egfolder\4.txt=null,e4ec861d34ab491b5b8538ee693c70ccda97e2ac861b8719b0b1d2ffdfe69cd7

    [8baedf145e0717cf216d30f5bb75afc643326ab5]
    First commit
    2.txt=null,0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1
    1.txt=null,e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
```