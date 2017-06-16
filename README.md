# AGP_WA_Development
Development of the AGP Web Application.  Since this is a private repository, the notes in the README are different from what we'd have in a public repository.

##Repository Branches: 

  * `master` - what will be deployed to the server
  * `development` - what we will work from

## Checking out from GitHub into Eclipse

1. In Eclipse, install the eGit plugin (under Eclipse marketplace)
2. Go to `Window` -> `Show View` -> `Other` -> `Team` -> `Git` -> `Git Repositories`
3. Click "Clone a Git Repository"
   * Add the link for this repository (https://github.com/sprenks18/AGP_WA_Development.git)
      * Enter your username and password for Github
   * Select the `development` branch
   * The defaults are probably okay on this screen, BUT make sure to check the "Import all existing Eclipse projects after clone finishes" checkbox
   * Click `Finish`
4. Import the project
   * In the Git repository view, you should see the repository
   * Expand "Working Tree"
   * Right-click on "Graffiti" and select "Import projects"
   * All of the defaults should be fine.  Click "Finish"
  
## Setting up project in Eclipse

After following the directions for checking out the project, you're ready to get set up within Eclipse:

1. Expand the project.  You should see a typical Web Application project folder.  You'll see greater than marks and question marks on folders/files that were added.
2. Ignore files that shouldn't be part of the shared repository because they're specific to you --> This may not be an issue for you, if I have the `.gitignore` file set up appropriately
    * Right-click on the following files/directories and select "Team" --> "Ignore": `.classpath` `.project` `.settings`
3. Right-click on the project, go to [Maven](https://maven.apache.org/), then select "Update Project".  Click ok to update the project.
4. Right-click on the project, go to "Run As" and select "Run on Server" and run on the server as usual.

## Our Workflow
When you work on something new, 

1. create a new branch (locally), named by that feature
2. develop, adding and removing files, and committing your changes as you go
3. develop code until you are satisfied with it and do a final commit -- in your commit comments, include the JIRA issue id
4. switch to the development branch
5. merge the branch you created into the development branch
6. push the development branch to GitHub

The `development` branch will be deployed to the development server, periodically.  We'll need to test this code to make sure things are working.  I'll test, but I need you all to test too.
 
Eventually, when we are satisfied that the code is all working and ready, we'll merge the development branch into the `master` branch and push the master branch to GitHub.  The master branch will be what will be deployed on the production server.
