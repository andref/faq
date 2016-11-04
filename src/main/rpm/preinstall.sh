if [ $1 -eq 1 ] ; then

    #
    # PRIMEIRA INSTALAÇÃO
    #

    echo "Instalação inicial"

    # Cria o usuário e o grupo

    getent group "${faq.group}" >/dev/null || groupadd -r "${faq.group}"
    getent passwd "${faq.user}" >/dev/null || useradd -r -m -g "${faq.group}" -d "${faq.home}" -c "Usuario FAQ" "${faq.user}"

    # Torna o home do serviço aberto para leitura

    chmod 770 "${faq.home}"

elif [ $1 -gt 1 ]; then

    #
    # ATUALIZAÇÃO DO PACOTE
    #

    echo "Instalando para atualização"

fi
