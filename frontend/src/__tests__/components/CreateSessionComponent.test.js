import {createRoot} from "react-dom/client";
import CreateSessionComponent from "../../components/CreateSessionComponent";
import ReactTestUtils, {act} from "react-dom/test-utils";

globalThis.IS_REACT_ACT_ENVIRONMENT = true;
let container = null;
let root = null;

beforeEach(() => {
  // setup a DOM element as a render target
  container = document.createElement("div");
  root = createRoot(container);
});

afterEach(() => {
  // cleanup on exiting
  container.remove();
  container = null;
});

it("creation of session for new user", () => {
  // Check if everything is rendered correctly
  act(() => {
    root.render(<CreateSessionComponent/>);
  });
  const nameInput = container.querySelector("[data-test-id='create-session-input-name']");
  const submitButton = container.querySelector("[data-test-id='create-session-submit']");

  expect(nameInput).toBeTruthy();
  expect(submitButton).toBeTruthy();
  expect(container.querySelector("[data-test-id='create-session-name']")).toBeNull();

  // Submit name
  const dispatchEventSpy = jest.spyOn(document, 'dispatchEvent');

  act(() => {
    ReactTestUtils.Simulate.change(nameInput, {target: {value: 'test'}});
    submitButton.dispatchEvent(new MouseEvent("click", {bubbles: true}));
  });

  expect(dispatchEventSpy).toHaveBeenCalledWith(expect.stringContaining("mauz"));
});
