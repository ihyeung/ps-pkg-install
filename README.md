# Package Installer

### Package Installer Requirements:
1. Input format is valid.
2. Inputs containing cyclic dependencies should be rejected as invalid.
3. Output should return a package installation order such that any given
  package's dependency precedes that package in the order of installation.

#### Program Input Specifications:
- Input is an array of strings, where each string in the array is in
  the format `"packagename: dependencyname"` or `"packagename: "`.
- Any package has at most one dependency.
- Only strings containing letters without any numbers, spaces, or special characters are considered valid package/dependency names (e.g. `"package-name: >=2.0.1"` would be handled as invalid input.)

#### Program Output Specifications:
- Input with invalid format or containing a cyclic dependency: `"Invalid Input"`
- Inputs that are valid will return a comma-separated string of package names
  in order of installation order.

##### Build Instructions:
1. Navigate to root dir with pom.xml file
2. `'mvn clean test'`
