new Vue({
    el: '#app',
    data: {
        todos: [],
        msgs: [],
        newTodo: {},
        newMsg: {}
    },
    created: function () {
        this.loadTodos();
        this.loadMsgs();
    },
    methods: {
        loadTodos() {
            let self = this;
            $.getJSON("api/todos", function (data) {
                self.todos = data
            });
        },
        saveTodo() {
            let self = this;

            $.ajax({
                type: "POST",
                url: 'api/todos',
                data: JSON.stringify(this.newTodo),
                contentType: "application/json",
                success: function () {
                    self.newTodo = {};
                    self.loadTodos();
                }
            });
        },
        deleteTodo(id) {
            let self = this;

            $.ajax({
                type: "DELETE",
                url: 'api/todos/' + id,
                success: function () {
                    self.loadTodos();
                }
            });
        },

      loadMsgs() {
        let self = this;
        $.getJSON("api/messages", function (data) {
          self.msgs = data
        });
      },
      saveMessage() {
        let self = this;

        $.ajax({
          type: "POST",
          url: 'api/messages',
          data: JSON.stringify(this.newMsg),
          contentType: "application/json",
          success: function () {
            self.newMsg = {};
            self.loadMsgs();
          }
        });
      },
      deleteMessage(id) {
        let self = this;

        $.ajax({
          type: "DELETE",
          url: 'api/messages/' + id,
          success: function () {
            self.loadMsgs();
          }
        });
      }
    },
    computed: {}
});
