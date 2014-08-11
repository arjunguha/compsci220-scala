CMPSCI220 Website
=================

Build Instructions
------------------

1. You'll need to install [Jekyll] website.

1. The website hosts Scaladoc that is generated from the support code. You must
   checkout both the `website` and `support-code` repositories alongside each
   other:

   ```
   ..
   |
   +- support-code (github.com/cmpsci220/support-code)
   |
   +- website (this repository)
   ```

1. In the `support-code` directory, type `sbt doc`.

1. In the `website` directory (this directory), type `make`.

The `make publish` command is hardcoded to upload the website to Arjun's
website.

[Jekyll]: http://jekyllrb.com
[Pygments]: http://pygments.org
