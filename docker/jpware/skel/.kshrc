#! /bin/bash
#______________________________________________________________________________
#
#       Environment file  - Bash shell - Jean-Paul Le F�vre
#______________________________________________________________________________

if [ -f $HOME/share/kshrc ]; then
    . $HOME/share/kshrc
else
    echo "$HOME/share/kshrc not found !"
fi
alias make='\make -f Makefile'

#______________________________________________________________________________
