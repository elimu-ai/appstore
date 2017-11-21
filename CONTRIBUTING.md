# Contributing to elimu.ai

The purpose of elimu.ai is to provide _every child_ with access to quality basic education. We 
believe that a free quality education is the right of every child no matter her social or 
geographical background.

For this reason, we build tablet-based software that teaches a child to read, write and calculate 
fully autonomously, without guidance from qualified teachers.

Since elimu.ai is an open source project, anyone is welcome to contribute towards this goal. We 
welcome different skills, cultures, perspectives, attitudes, and experiences.

This document explains the step-by-step process and guidelines for contributing to any of the 
repositories in the elimu.ai [GitHub organization](https://github.com/elimu-ai).

Each repository has a maintainer responsible for reviewing code submitted by contributors. 
The appointment of maintainers and lead maintainers is based on previous contributions (meritocracy).

## Contributor workflow

### Outside Contributor vs Core Contributor

The first step is to decide what _type_ of contributor you are. In general, every contributor would 
belong to one of the following:

   1. _Outside_ contributor
   
      Being an outside contributor means that you have not been added to the 
      elimu.ai [GitHub organization](https://github.com/elimu-ai). In practice this means that you 
      do not have access to create new repositories or repository branches.
      
   2. _Core_ contributor

      Being a core contributor means that you have been added to the elimu.ai 
      [GitHub organization](https://github.com/elimu-ai). This gives you access to create new 
      repositories and repository branches.

### How to Know Which Task to Work On

   1. Each application has its own GitHub repository. Start by identifying a repository in which you 
   would like to contribute: https://github.com/elimu-ai
   
   2. Look at the code in the repository to see if your skills match those required. If in doubt, 
   post your questions in the [Slack channel](http://slack.elimu.ai) or send an e-mail to 
   info@elimu.ai.
   
   3. When you have selected a repository, click the 
   ["Projects"](https://github.com/elimu-ai/appstore/projects) tab of the repository. Within each 
   project you will see a collection of issues, and the state of each one (_todo_, _in progress_, 
   _done_). Feel free to start working on any issue in the left-hand "To do" column.
   
   4. If you feel lost, take a look at the issues labeled with `good first issue`. These are tasks 
   that are considered as good entry-points for first-time contributors. See 
   [example](https://github.com/elimu-ai/appstore/issues?q=is%3Aissue+is%3Aopen+label%3A"good+first+issue").
   
   5. You are also welcome to create new GitHub issues yourself. For example, if you have discovered 
   a bug, or want to suggest a new feature, press the "New issue" button.
      
### How to Implement and Submit Your Work

Once you have identified a task to work on, there is a specific workflow that you should follow. This 
workflow is mostly identical for both outside and core contributors, except from the way to create a 
new GitHub branch to work on:

   1. If you are an outside contributor, _fork_ the repository to your own GitHub account. (If you are 
   are core contributor, you will create a new branch directly in the main repository.)

   2. Create a new _branch_ for the GitHub issue that you will be working on. If, for example the issue 
   you have picked is titled "Upgrade to the latest Gradle version" and has the issue number 57, 
   create a new branch with the title "57 Upgrade to the latest Gradle version". By having the issue 
   number in the branch name, it's easier to understand which issue the branch is for.
   
   3. Remember to include a reference to the GitHub issue number for each commit to make it easier for 
   future contributors to understand each code change. E.g. "#57 Upgraded to Gradle 4.1".
   
   4. Keep each commit small so that diffs are easy to read and understand for people looking at your 
   code in the future. A pull request should always be focused to doing one thing, for example fixing 
   a bug or adding a new feature, but not a mixture. Avoid large commits and pull requests which 
   attempt to do too much at once, as this makes code review difficult.
   
   5. If you want to add things like formatting fixes or refactoring, etc, do _not_ mix those with 
   the actual code change related to an issue. To make the actual code change easier to read, separate 
   refactoring into a new branch (and a new issue).
   
   6. Once you are ready to receive feedback on your branch, create a _pull request_ for merging it 
   into the `master` branch.

   7. Every pull request requires at least one approved peer review before being merged, so add at 
   least one person as a _code reviewer_ of your pull request. To find out who to add, click 
   "Insights" → "Contributors" too see an overview of the most active contributors for each 
   repository. If you are unsure who to add, use these: `jo-elimuai` and `sladomic`.
   
   8. Once your pull request has been reviewed and approved, it can be merged. The person who created 
   the branch (you) will be the person responsible for merging the branch. So the peers reviewing 
   your code will not merge the branch; that is your responsibility.
   
   9. If you consider the issue complete (code reviewed, merged, and tested), delete the branch, and 
   move the GitHub issue to the "Done" column. Well done!

If any of the above steps are unclear, or you have any other questions or comments, please reach out 
to the other community members in the [Slack channel](http://slack.elimu.ai). Thank you for 
contributing 😀
