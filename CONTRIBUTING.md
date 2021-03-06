# How to contribute

We love pull requests and we want to make it as easy as possible to contribute changes.

## Getting started
* Make sure you have a [GitHub account](https://github.com/).
* Maybe create a GitHub issue (TODO: link): is this a comment or documentation change? Does an issue already exist? If you need an issue then describe it in as much detail as you can, e.g. step-by-step to reproduce.
* Fork the repository on GitHub.
* Clone the repo: `git clone TODO`
 * Create a branch for your change, probably from the master branch. Please don't work on master. Try this: `git checkout -b fix/master/my_contribution master`

## Making changes
* New elements should have `<xs:annotation>` elements attached to them to provide documentation about the element.
* The revised schema should be valid XML and successfully validate against the XMLSchema standard.
* New elements should in most cases be optional as not all source systems can provide all data fields. Enforcing mandatory data can be done outside of the schema.

## Sumitting changes
* Sign the Contributor Licence Agreement (TODO: link)
* Push your changes to your fork.
* Submit a pull request (TODO: link)
* We'll look at it pretty soon after it's submitted, and we aim to respond within one week. 

## Getting it accepted
Here are some things you can do to make this all smoother:
* If you think it might be controversial then discuss it with us beforehand, via a GitHub issue.
* Write a [good commit message](http://chris.beams.io/posts/git-commit/).
