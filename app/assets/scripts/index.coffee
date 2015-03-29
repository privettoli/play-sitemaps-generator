$('#generateButton').click ->
  urlPath = '/sitemaps'
  urlText = $('#urlText').val();
  depth = $('#depth').val();
  fullUrl = urlPath + "?incomingUrl=#{urlText}"
  if depth.length > 0
    fullUrl += "&depth=#{depth}"
  $.ajax({
    type: 'POST',
    url: fullUrl
  }).done((result) ->
    link = $('<a>')
    link.attr('href', result.downloadAsZipUrl)
    link.text(result.url)
    $('#links').append link
    $('#links').append $('<br>')
  ).fail((result) ->
    window.alert(result.responseText);
  )
###
initUi = ->
  if $(".error")?
    $(".info").hide()

initWebSocket = ->
  webSocket = new WebSocket $("#new-posts-ws-url").val()
  webSocket.onmessage = (evt) ->
    newPostEvent JSON.parse evt.data

newPostEvent = (post) ->
  if post.isCode
    $('#posts').prepend $("<li>").append $("<pre>").append $("<code>").text post.message
  else
    $('#posts').prepend $("<div class='notCode'>").append $("<li>").text post.message
  null

initPostsLoader = ->
  $.get $("#last-ten-posts-url").val(), (data) ->
    for post in data.reverse()
      newPostEvent post
    null

initSavePost = ->
  $("#save-post").click ->
    message = $("#message")
    isCodeInput = $("#isCodeInput")
    $.ajax({
      type: 'POST',
      url: $('#save-post-url').val(),
      data: {
        message: message.val(),
        isCode: isCodeInput.prop("checked")
      }
    }).done(->
      isCodeInput.prop("checked", false)
      message.val('')
      message.focus()
    ).fail(->
      alert "Something went wrong…"
    )


$(document).ready ->
  initUi()
  initWebSocket()
  initPostsLoader()
  initSavePost()###
