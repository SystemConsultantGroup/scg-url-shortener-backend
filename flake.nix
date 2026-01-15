{
  description = "Development environment for URL Shortener Backend";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            jdk17
            gradle
          ];

          shellHook = ''
            export JAVA_HOME=${pkgs.jdk17}
            echo "Environment set up with JDK 17 and Gradle."
          '';
        };
      }
    );
}
