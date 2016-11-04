if [ $1 -eq 0 ] ; then

    #
    # DESINSTALAÇÃO DO PACOTE
    #

    echo "Finalizando desinstalação para remoção completa"

    # Remove usuário e grupo (o home continua lá.)

    userdel -r -f "${faq.user}" > /dev/null 2>&1 || :
    groupdel "${faq.group}" > /dev/null 2>&1 || :

elif [ $1 -eq 1 ] ; then

    #
    # ATUALIZAÇÃO DO PACOTE
    #

    echo "Finalizando desinstalação para atualização"

fi
