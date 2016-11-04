
function NotesFactory ($resource) {
    return $resource('/categorias/:id', {
        id: '@id'
    })
}

class CategoriaController {
    constructor (Notes) {
        console.log('Hello!')
        this.categorias = Notes.query()
    }
}

angular
    .module('faq', ['ngResource', 'ui.router'])
    .controller('CategoriaController', CategoriaController)
    .factory('Notes', NotesFactory)
