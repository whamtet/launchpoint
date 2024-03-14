const {assert} = require('chai');
const {
    clickButton,
    setInput,
    clickInput,
    clickInputValue,
    typeInput,
    typeTextArea,
    setSelect,
    innerText
} = require('./util');

async function canEditName(page) {
    // test button edit
    await clickButton(page, 'Edit');
    await page.waitForNetworkIdle();

    await setInput(page,'first_name', 'Testy');
    await clickInputValue(page, 'Save');
    await page.waitForNetworkIdle();

    assert.equal(await innerText(page, '#names'), 'Testy User');
}

async function canEditDescription(page) {

    await page.type('textarea', 'Experienced worker.');
    await page.click('#description-title');
    await page.waitForNetworkIdle();
    await page.reload();

    const updatedDescription = await page.$eval('textarea', e => e.value);
    assert.equal(updatedDescription, 'Experienced worker.');

}

async function addEarlierJob(page) {

    await clickButton(page, 'Add Job');
    await page.waitForNetworkIdle();
    await typeInput(page, 'title', 'Manager');
    await page.type('#company-search-box', 'Fine Organics');
    await typeInput(page, 'from-year', '2000');
    await typeInput(page, 'to-year', '2001');
    await typeTextArea(page, 'description', 'Made $$$!');
    await clickInputValue(page, 'Save');

}

async function addJob(page) {

    await clickButton(page, 'Add Job');
    await page.waitForNetworkIdle();
    await typeInput(page, 'title', 'CEO');
    await setInput(page,'company', 'ACME INC.');
    await clickInput(page, 'present');
    await page.waitForNetworkIdle();

    await typeInput(page, 'from-year', '2002');
    await setSelect(page, 'from-month', '1');

    await typeTextArea(page, 'description', 'Made even more $$$!');
    await clickInputValue(page, 'Save');

}

async function canAddJobs(page) {

    await addJob(page);
    await page.waitForNetworkIdle();
    await addEarlierJob(page);
    await page.waitForNetworkIdle();

    assert.equal(await innerText(page, '#work-history'), 'CEO\nEdit Job\nDelete Job\nACME INC.\nJanuary 2002 - Present\nMade even more $$$!\nManager\nEdit Job\nDelete Job\nFine Organics\n2000 - 2001\nMade $$$!');

}

async function canEditJob(page) {

    await clickButton(page,'Edit Job');
    await page.waitForNetworkIdle();
    await setInput(page,'company', 'Finolex Industries');
    await clickInputValue(page, 'Save');
    await page.waitForNetworkIdle();

    assert.equal(await innerText(page, '#work-history'), 'CEO\nEdit Job\nDelete Job\nFinolex Industries\nJanuary 2002 - Present\nMade even more $$$!\nManager\nEdit Job\nDelete Job\nFine Organics\n2000 - 2001\nMade $$$!');

}

async function canAddEducation(page) {

    await clickButton(page, 'Add Education');
    await page.waitForNetworkIdle();
    await setInput(page, 'degree', 'LLB (Hons)');
    await setInput(page, 'institution', 'Havard');
    await setInput(page, 'year', '2003');
    await clickInputValue(page, 'Save');
    await page.waitForNetworkIdle();

    assert.equal(await innerText(page, '#education-history'), 'LLB (Hons)\nEdit Education\nDelete Job\nHavard\n2003');

}

async function canEditEducation(page) {

    await clickButton(page, 'Edit Education');
    await page.waitForNetworkIdle();
    await setInput(page, 'year', '2004');
    await clickInputValue(page, 'Save');
    await page.waitForNetworkIdle();

    assert.equal(await innerText(page, '#education-history'), 'LLB (Hons)\nEdit Education\nDelete Job\nHavard\n2004');
}

async function profile(page) {

    await page.goto('http://localhost:3001/profile/');

    await canEditName(page);
    await canEditDescription(page);
    await canAddJobs(page);
    await canEditJob(page);
    await canAddEducation(page);
    await canEditEducation(page);

}

exports.profile = profile;
