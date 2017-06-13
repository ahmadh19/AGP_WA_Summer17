# AGP_WA_Development
Development of the AGP Web Application

## Checking out from GitHub into Eclipse

1. In Eclipse, install the eGit plugin (under Eclipse marketplace)
2. Go to `Window` -> `Show View` -> `Other` -> `Team` -> `Git` -> `Git Repositories`
3. Click "Clone a Git Repository"
   * Add the link for this repository (https://github.com/sprenks18/AGP_WA_Development.git)
      * Enter your username and password for Github
   * Select the master branch
   * The defaults are probably okay on this screen, BUT make sure to check the "Import all existing Eclipse projects after clone finishes" checkbox
   * Click `Finish`
4. Import the project
   * In the Git repository view, you should see the repository
   * Expand "Working Tree"
   * Right-click on "Graffiti" and select "Import projects"
   * All of the defaults should be fine.  Click "Finish"
  
## Setting up project in Eclipse

After following the directions for checking out the project, you're ready to get set up within Eclipse:

1. Expand the project.  You should see a typical Web Application projecjt folder.  You'll see greater than marks and question marks on folders/files that were added.
2. Ignore files that shouldn't be part of the shared repository because they're specific to you.
    * Right-click on the following files/directories and select "Team" --> "Ignore": `.classpath` `.project` `.settings`
3. Right-click on the project, go to [Maven](https://maven.apache.org/), then select "Update Project".  Click ok to update the project.
4. Right-click on the project, go to "Run As" and select "Run on Server" and run on the server as usual.
