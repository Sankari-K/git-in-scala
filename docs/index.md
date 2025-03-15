# Welcome to my implementation of git

I've been using Git for quite some time, and I thought it would be fun to gain a deeper understanding of how it works by attempting to rebuild it based on my interpretation of its functionality.

## Introduction

This project is a personal implementation of Git, the popular version control system. It’s written in Scala and aims to provide similar functionality to Git while exploring the underlying principles of how version control systems work.

### Commands implemented:
* `init <directory>` - Creates a new git repository. [(git man page)](https://git-scm.com/docs/git-init)
* `add <files>` - Adds a file to the staging area. [(git man page)](https://git-scm.com/docs/git-add)
* `commit <message>` - Record changes to the repository. [(git man page)](https://git-scm.com/docs/git-commit)
* `amend--message` - Changes commmit message of latest commit [(git man page)](https://git-scm.com/book/en/v2/Git-Basics-Undoing-Things)
* `amend--commit` - Add additional staged changes to the commit [(git man page)](https://git-scm.com/book/en/v2/Git-Basics-Undoing-Things)
* `status` - Show the working tree status of the repository. [(git man page)](https://git-scm.com/docs/git-status)
* `log` - Show all commits made. [(git man page)](https://git-scm.com/docs/git-log)
* `restore <files>` - Restore working tree files. [(git man page)](https://git-scm.com/docs/git-restore)
* `restore--staged <files>` - Restore content in the index. [(git man page)](https://git-scm.com/docs/git-restore)
* `checkout <hash>` - Restore working tree files to the given hash. [(git man page)](https://git-scm.com/docs/git-checkout)
* `rm <files>` - Remove files from the worktree and index. [(git man page)](https://git-scm.com/docs/git-rm)
* `config <key> <value>` - Sets the `key`'s value to be equal to `value` in git's config file.
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

The index looks like a `String => (String, String)` map. The key is the path of the file, and the tuple represents the `old` and `new` SHA hashes of the file. The `oldhash` is the state of the file from the latest commit (or `null` if it didn't exist) and the `newhash` is the state of the file from the latest `git add` operation.

For example, an index file would look like this:
```plaintext
    1.txt:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855:2860d7deca71859f9fae69da862b3934b772f24d23137f326a030bf042dc8d7d
    2.txt:0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1:0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1
    3.txt:null:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    egfolder\4.txt:null:e4ec861d34ab491b5b8538ee693c70ccda97e2ac861b8719b0b1d2ffdfe69cd7
```
A commit is just a snapshot of an index file with an additional commit message, author name and email, a timestamp of the commit, and a unique commit hash that is generated. It would look like this:

```plaintext
    [da563b40a3e49923b50a173b0cc73c40627a0430]
    Second commit
    sankari
    sankarikarthik03@gmail.com
    sunday march 9 20:47:35 2025 +0530
    1.txt=e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855,2860d7deca71859f9fae69da862b3934b772f24d23137f326a030bf042dc8d7d
    2.txt=0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1,0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1
    3.txt=null,e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    egfolder\4.txt=null,e4ec861d34ab491b5b8538ee693c70ccda97e2ac861b8719b0b1d2ffdfe69cd7

    [8baedf145e0717cf216d30f5bb75afc643326ab5]
    First commit
    sankari
    sankarikarthik03@gmail.com
    sunday march 9 20:42:27 2025 +0530
    2.txt=null,0051eb1be2439f3db88107f1c8643256d331f3078dc5d51b7c40acfeb03c88b1
    1.txt=null,e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
```

### Objects

All files tracked by git must have a corresponding file in the `.wegit/objs` directory. As mentioned earlier, all file objects are represented by a SHA hash. The contents of the file are compressed (using zlib) and stored in the `objs/` directory, where the name of the file is its SHA hash.

## Usage

### An executable
A [jar file](https://en.wikipedia.org/wiki/JAR_(file_format)) can be created using the scala build tool, called `sbt`. 

### Testing
For testing, running `scala run *.scala` in the same directory as the program should print out the usage.

## Creating repositories: init

There are two ways this command can be executed - by leaving it blank (`scala run *.scala -- init`) in which case it creates a new git repo in the same directory, or by explicitly mentioning the directory where the git repo needs to be created.

This command first checks if the given directory is already an existing repo (by checking for the existence of a `.wegit` folder). If it is, an error message is printed out. Otherwise, a `.wegit` directory and a nested `objs/` folder is created.

### Note
In all the following commands, a check is done to make sure the directory is a git repository before executing the command. If it isn't an existing repo, an appropriate error message is printed out.

## Adding files to the staging area: add

This command can take multiple files as its argument. After getting "added", their modifications are tracked by git. 

This is done in code by iterating through all files in the directory and updating the file's `newhash` by calculating the hash of its current contents. The `oldhash` remains the same. If a file didn't exist earlier, the `oldhash` is `null`. The hash generated for an empty file is a different, non-null value.

This takes care of a few things:

* If a file is added but has no modifications, no changes are made in the `INDEX` file.
* If there are multiple edit-add cycles on the same file (without any `commit`s), we only care about the latest content of the file - not the contents in the intermediate stages.
* If a file is deleted, we can track that change nicely by just changing the `newhash` to `null`.
* Similarly, a new file being staged can be detected by checking if the `oldhash` is `null`.

After the INDEX file is updated, the file's compressed contents are stored in the `objs/` folder with the name being its `SHA` hash.

## Saving changes to history: commit

A `commit` is expected to take a snapshot of the current `INDEX` file and store it along with more useful information (like, a message). If an `INDEX` file doesn't exist already, the operation is terminated. Otherwise, a commit hash is generated which takes the `INDEX` contents (yes, all of it) as opposed to just the file names or any other variation as its argument. 

* So, if there are any changes to the `INDEX`, that guarantees a different commit hash. 
* No changes to the `INDEX` would lead to the commit hash being the same. This is a nice way to check for changes in the `INDEX`.

### Changes to data structures 
This operation does two things:

* Makes a commit with the generated commit hash (essentially copies the current contents in the `INDEX` file and makes an entry in the `COMMIT` file).
* Iterates over the `INDEX` and updates it - both the `oldhash` and the `newhash` are populated with the `newhash` value. Any file with `newhash` as `null` is a deleted file, so it gets removed from the `INDEX`.

---
**NOTE**

> 📣 "No changes to the `INDEX` would lead to the commit hash being the same. This is a nice way to check for changes in the `INDEX`."
   This is true in principle. However, consider this case:
   
```python
    scala run ./*.scala -- add 1.txt
    scala run ./*.scala -- commit "Added file"
    scala run ./*.scala -- commit "Is this possible"
```
> Ideally, the second commit should not be possible. But the `commit` operation updates the `oldhash` to be the `newhash`, so the `INDEX` technically changes and a commit would be possible.

> Similarly in this situation:
```python
    scala run ./*.scala -- rm 1.txt
    scala run ./*.scala -- commit "Deleted file"
    scala run ./*.scala -- commit "Is this possible"
```

> These cases are taken care of by explicitly checking the `INDEX` for changes before a commit, and not just the hash of the `INDEX` contents.

---

## Rewriting a commit: amend

### Rewriting just the commit message: amend-message
To just change the commit message of the latest commit, this command can be used. This doesn't change the commit hash, but changes the timestamp of the commit.

### Adding more staged files to the last commit: amend--commit
This command can be used if a commit is done too early and one possibly forgets to add some files. This is done by fetching the latest commit,
iterating over the current `INDEX`.

```plaintext
    for all (file, (oldhash, newhash)) in index:
        lastoldhash, lastnewhash = previousIndex
        index.update(file, (lastoldhash, newhash))
```
One case to be considered - if the file was deleted as part of the previous commit, there won't be an entry in the current `INDEX` - in that case, the old index must be iterated through to update the `INDEX`.

```plaintext
    for all (file, (lastoldhash, lastnewhash)) in previousIndex:
        if file not in index:
            index.update(file, (lastoldhash, lastnewhash))
```

The latest commit is removed, and a new commit hash is generated based on the updated `INDEX`. Depending on whether a message was provided or not, the given message or the previous message is taken to create a commit. After the commit, the `INDEX` is updated like the commit section earlier.

## Checking the state of the repository: status

### Changes to be committed

The `INDEX` is iterated over and - 

* New files are found by making sure `oldhash` is `null` (file didn't exist earlier) and `newhash` isn't `null` (file was deleted after staging it).
* Modifed files are found by making sure `oldhash` and `newhash` aren't `null`, and `oldhash` is different from `newhash`.
* Deleted files are found by making sure `newhash` is `null`.

### Changes not staged for commit

All files in the repo are iterated over (and if it is present in the `INDEX`) - 

* The current contents of the file are found, and the hash is computed. If that isn't equal to the `newhash` present in the `INDEX` for that file, there are untracked modifications.

### Untracked files

All files in the repo are iterated over -

* If the file is not present in the `INDEX`, it is an untracked file.

Implementing `git status` was quite straightforward thanks to thorough brainstorming and choosing the right modules and data structures. This taught me the importance of spending time upfront on basic architecture.


## Reading commit history: log

This just iterates through all `commit`s made and prints out each one's hash and message.

## Restoring working tree files: restore

This command deletes working tree changes and restores back the previously staged changes. This is done by getting the `newhash` from the `INDEX` for the given file (latest version that was added), and replacing the current contents of the file with the contents pointed to in the `objs/` directory by the `newhash`.

### Subcommand: restore--staged
This command unstages local changes by removing the specified file(s) from the `INDEX`.

## Restoring working tree files to a hash: checkout

Before the command is run, it makes sure there are no local changes that haven't been committed. Unmodified changes are checked by:

* Checking the diff between the `INDEX` and current state of files
* Checking the diff in `INDEX` to check for uncommitted changes.

Then, the current `INDEX` is modified to look like the particular commit referenced by the hash given. If the `commithash` doesn't match any commit, the checkout is aborted. 

## Removing files: rm

This command deletes the specified file and updates that information in the `INDEX` => it stages the "deletion".
That is done by updating the `newhash` for that file to `null` and deleting the file from the file system.

If there are staged changes in the `INDEX` for that file, the operation is aborted.

## Setting up repository constants: config

This is used to save global variables about the repo, such as the user's name and email (which are used for commit operations).

## Ignore files

To ignore a file, a `.ignore` file can be created in the root directory. A file can't be `add`ed if it is in the `.ignore` file.

## If nothing works: pls-work

(self-explanatory)

## To do

There are quite a few things I'd like to build upon. This section keeps track of ideas, improvements, and features I’d like to implement in the future (or, as I like to call it, a staging area for future ideas :P).

- Make an executable
- Make sure executable can be run from anywhere
- Add must be able to take in 

    * current directory ("." or no params) 
    * any directory (relative or absolute)
    * multiple files

- In general, wegit must run inside any (nested) part of the repo
- Implement .gitignore **[DONE]**
- Move the prints to a separate module (a log/message/error handler)
- Implement git delete (and make sure status captures it)
- Add parameters like author, timestamp for a git commit **[DONE]**
- Go through modules made and delete unnecessary helper functions **[DONE]**

- Implement all of git checkout

    * Create a `HEAD` file (to implement the logic for "detached head")

- Create a git commit--amend command
- Create docstrings and pull 'em in for the help/usage command
- Document the system design, modules, usage, and installation on mkdocs
- Debug this situation - a commit has been made, but no changes in the `INDEX`. Currently, the commit message and hash are created (but aren't being modified in the `INDEX`). **[DONE]**
- Optimize the "changes not staged for commit" functionality in git status. There's no need to iterate over all files in the repository. **[DONE]**
