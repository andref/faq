if [ $1 -eq 1 ] ; then

    #
    # PRIMEIRA INSTALAÇÃO
    #

    echo "Finalizando instalação inicial"

    # Abre o firewall

    firewall-cmd --permanent --add-port "8080/tcp" > /dev/null 2>&1
    firewall-cmd --add-port "8080/tcp" > /dev/null 2>&1

    # Habilita os serviços

    systemctl enable faq.service > /dev/null 2>&1 || :

    # Inicializa o banco de dados

    sudo -u "${faq.user}" /usr/bin/java -jar ${faq.home}/lib/faq.jar db migrate ${faq.home}/etc/config.yaml

    # Roda o serviço

    systemctl start faq.service > /dev/null 2>&1 || :

elif [ $1 -gt 1 ]; then

    #
    # ATUALIZAÇÃO DO PACOTE
    #

    echo "Finalizando instalação para atualização"

    # Reinicia o serviço

    systemctl restart faq.service > /dev/null 2>&1 || :

fi
