exports.typeInput = (page, name, text) => page.type(`input[name="${name}"]`, text);
exports.typeTextArea = (page, name, text) => page.type(`textarea[name="${name}"]`, text);
exports.clickInput = (page, name) => page.click(`input[name="${name}"]`);
exports.clickInputValue = (page, value) => page.click(`input[value="${value}"]`);
exports.clickButton = (page, text) => page.click(`xpath/.//button[contains(text(), '${text}')]`);
exports.setInput = (page, name, text) => page.$eval(`input[name="${name}"]`, (input, text) => input.value = text, text);
exports.setSelect = (page, name, value) => page.select(`select[name="${name}"]`, value);

exports.innerText = (page, selector) => page.$eval(selector, e => e.innerText);
