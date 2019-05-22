#! /bin/ksh
#______________________________________________________________________________
#
#	Environment file  - Bash shell - Jean-Paul Le Fèvre
#______________________________________________________________________________

alias m=more
alias h=history
alias rm="\rm -i"
alias a=alias
alias ll='/bin/ls -lt'
alias fonc='typeset -f'
alias ll='/bin/ls -lt'
alias ml='more liste~'
alias tl='tail liste~'
alias df='df -h'
alias du='du -sm'
alias pwd='echo Directory : $PWD'

alias wcf='cd /home/lefevre/work/git/configurations; pwd'
alias date='date "+%H h. %M m. %S s. %tle %d %h %y"'
alias chuw='chmod u+w'
alias chaw='chmod a-w'
alias qui='echo Tu es `whoami` sur `hostname`, idiot !'

alias purge='find . \( -name "*%" -o -name ".*%" -o -name "*~" -o -name ".*~" -o
 -name "#*#" -o -name "core" -o -name "*.out" -o -name ",*" -o -name "*pure_*" \
) -exec rm -i {} \;'
alias Purge='find . \( -name "*%" -o -name "*~" -o -name ".*~" -o -name ".#*" -o
 -name "core" -o -name "nohup.out" \) -print -exec rm {} \;'

alias wcd='WORK=$PWD'
alias shutr='shutdown -r now'
alias shuth='shutdown -h now'
alias cpan='perl -MCPAN -e shell'
alias emacs='emacs -u lefevre'

alias dtm='cd ${CATALINA_HOME}; pwd'
alias dlog='cd ${CATALINA_HOME}/logs; pwd'

#______________________________________________________________________________

function psu
{ 
    if [ -z "$1" ]; then
        u=$USER
    else
        u=$1
    fi

    ps -U $u -o "%U %p %a"
}
#______________________________________________________________________________
