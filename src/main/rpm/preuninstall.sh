
if [ $1 -eq 0 ] ; then

    #
    # DESINSTALAÇÃO DO PACOTE
    #

    echo "Desinstalando para remoção completa"

    # Fecha o firewall

    firewall-cmd --permanent --remove-port "8080/tcp"
    firewall-cmd --remove-port "8080/tcp"

    # Para o serviço

    systemctl stop faq.service > /dev/null 2>&1 || :

    # Desabilita o serviço

    systemctl --no-reload disable faq.service > /dev/null 2>&1 || :

elif [ $1 -eq 1 ] ; then

    #
    # ATUALIZAÇÃO DO PACOTE
    #

    echo "Desinstalando após atualização"

fi
